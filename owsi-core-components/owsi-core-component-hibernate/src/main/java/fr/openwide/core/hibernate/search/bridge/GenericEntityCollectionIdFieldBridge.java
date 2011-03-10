package fr.openwide.core.hibernate.search.bridge;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;

public class GenericEntityCollectionIdFieldBridge extends AbstractGenericEntityIdFieldBridge {
	
	@Override
	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		if (value == null) {
			return;
		}
		if (!(value instanceof Collection)) {
			throw new IllegalArgumentException("This FieldBridge only supports Collection of GenericEntity properties.");
		}
		Collection<?> objects = (Collection<?>) value;
		
		for (Object object : objects) {
			luceneOptions.addFieldToDocument(name, objectToString(object), document);
		}
	}
	
}