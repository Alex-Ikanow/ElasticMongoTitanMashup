#!/bin/bash

# Returns the absolute path of this script regardless of symlinks
abs_path() {
    # From: http://stackoverflow.com/a/246128
    #   - To resolve finding the directory after symlinks
    SOURCE="${BASH_SOURCE[0]}"
    while [ -h "$SOURCE" ]; do
        DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
        SOURCE="$(readlink "$SOURCE")"
        [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
    done
    echo "$( cd -P "$( dirname "$SOURCE" )" && pwd )"
}

BIN=`abs_path`
REXSTER_CONFIG_TAG=berkeleydb-es

: ${REXSTER_STARTUP_TIMEOUT_S:=60}
: ${REXSTER_SHUTDOWN_TIMEOUT_S:=60}
: ${REXSTER_IP:=127.0.0.1}
: ${REXSTER_PORT:=8184}

: ${SLEEP_INTERVAL_S:=2}
VERBOSE=
COMMAND=

# Locate the jps command.  Check $PATH, then check $JAVA_HOME/bin.
# This does not need to by cygpath'd.
JPS=
for maybejps in jps "${JAVA_HOME}/bin/jps"; do
    type "$maybejps" >/dev/null 2>&1
    if [ $? -eq 0 ]; then
        JPS="$maybejps"
        break
    fi
done

if [ -z "$JPS" ]; then
    echo "jps command not found.  Put the JDK's jps binary on the command path." >&2
    exit 1
fi

# wait_for_startup friendly_name host port timeout_s
wait_for_startup() {
    local friendly_name="$1"
    local host="$2"
    local port="$3"
    local timeout_s="$4"

    local now_s=`date '+%s'`
    local stop_s=$(( $now_s + $timeout_s ))
    local status=

    echo -n "Connecting to $friendly_name ($host:$port)"
    while [ $now_s -le $stop_s ]; do
        echo -n .
        $BIN/checksocket.sh $host $port >/dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo " OK (connected to $host:$port)."
            return 0
        fi
        sleep $SLEEP_INTERVAL_S
        now_s=`date '+%s'`
    done

    echo " timeout exceeded ($timeout_s seconds): could not connect to $host:$port" >&2
    return 1
}

# wait_for_shutdown friendly_name class_name timeout_s
wait_for_shutdown() {
    local friendly_name="$1"
    local class_name="$2"
    local timeout_s="$3"

    local now_s=`date '+%s'`
    local stop_s=$(( $now_s + $timeout_s ))

    while [ $now_s -le $stop_s ]; do
        status_class "$friendly_name" $class_name >/dev/null
        if [ $? -eq 1 ]; then
            # Class not found in the jps output.  Assume that it stopped.
            return 0
        fi
        sleep $SLEEP_INTERVAL_S
        now_s=`date '+%s'`
    done

    echo "$friendly_name shutdown timeout exceeded ($timeout_s seconds)" >&2
    return 1
}

start() {
    echo "Forking Titan + Rexster..."
    if [ -n "$VERBOSE" ]; then
        "$BIN"/rexster.sh -s -wr public -c ../conf/rexster-${REXSTER_CONFIG_TAG}.xml &
    else
        "$BIN"/rexster.sh -s -wr public -c ../conf/rexster-${REXSTER_CONFIG_TAG}.xml >/dev/null 2>&1 &
    fi
    wait_for_startup 'Titan + Rexster' $REXSTER_IP $REXSTER_PORT $REXSTER_STARTUP_TIMEOUT_S || {
        echo "See $BIN/../log/rexstitan.log for Rexster log output."  >&2
        return 1
    }
    disown
    echo "Run rexster-console.sh to connect." >&2
}

stop() {
    kill_class        'Titan + Rexster' com.tinkerpop.rexster.Application 
    wait_for_shutdown 'Titan + Rexster' com.tinkerpop.rexster.Application $REXSTER_SHUTDOWN_TIMEOUT_S
}

kill_class() {
    local p=`$JPS -l | grep "$2" | awk '{print $1}'`
    if [ -z "$p" ]; then
        echo "$1 ($2) not found in the java process table"
        return
    fi
    echo "Killing $1 (pid $p)..." >&2
    case "`uname`" in
        CYGWIN*) taskkill /F /PID "$p" ;;
        *)       kill "$p" ;;
    esac
}

status_class() {
    local p=`$JPS -l | grep "$2" | awk '{print $1}'`
    if [ -n "$p" ]; then
        echo "$1 ($2) is running with pid $p"
        return 0
    else
        echo "$1 ($2) does not appear in the java process table"
        return 1
    fi
}

status() {
    status_class 'Titan + Rexster' com.tinkerpop.rexster.Application 
}

clean() {
    echo -n "Are you sure you want to delete all stored data and logs? [y/N] " >&2
    read response
    if [ "$response" != "y" -a "$response" != "Y" ]; then
        echo "Response \"$response\" did not equal \"y\" or \"Y\".  Canceling clean operation." >&2
        return 0
    fi

    if cd "$BIN"/../db 2>/dev/null; then
        rm -rf cassandra es
        echo "Deleted data in `pwd`" >&2
        cd - >/dev/null
    else
        echo 'Data directory does not exist.' >&2
    fi

    if cd "$BIN"/../log; then
        rm -f rexstitan.log
        echo "Deleted logs in `pwd`" >&2
        cd - >/dev/null
    fi
}

usage() {
    echo "Usage: $0 [options] {start|stop|status|clean}" >&2
    echo " start:  fork Rexster+Titan processes" >&2
    echo " stop:   kill Rexster+Titan processes" >&2
    echo " status: print Rexster+Titan process status" >&2
    echo " clean:  permanently delete all graph data (run when stopped)" >&2
    echo "Options:" >&2
    echo " -v      enable logging to console in addition to logfiles" >&2
    echo " -c str  configure rexster with conf/rexster-<str>.xml" >&2
    echo "         recognized arguments to -c:" >&2
    shopt -s nullglob
    for f in "$BIN"/../conf/rexster-*.xml; do
        f="`basename $f`"
        f="${f#rexster-}"
        f="${f%.xml}"
        echo "           $f" >&2
    done
}

find_verb() {
    if [ "$1" = 'start' -o \
         "$1" = 'stop' -o \
         "$1" = 'clean' -o \
         "$1" = 'status' ]; then
        COMMAND="$1"
        return 0
    fi
    return 1
}

while [ 1 ]; do
    if find_verb ${!OPTIND}; then
        OPTIND=$(($OPTIND + 1))
    elif getopts 'c:v' option; then
        case $option in
        c) REXSTER_CONFIG_TAG="${OPTARG}";;
        v) VERBOSE=yes;;
        *) usage; exit 1;;
        esac
    else
        break
    fi
done

if [ -n "$COMMAND" ]; then
    $COMMAND
else
    usage
    exit 1
fi
