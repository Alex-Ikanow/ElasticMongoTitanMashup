package demo.elasticinsight_manager.events;

public interface IndexEventFactory {

	EsDeletedDocument onDeletedDocument();
	EsMappingChanged onMappingChanged();
	EsNewDocument onNewDocument();
	EsNewIndex onNewIndex();
}
