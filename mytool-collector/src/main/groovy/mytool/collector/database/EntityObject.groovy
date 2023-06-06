package mytool.collector.database

import groovy.transform.CompileStatic;


/**
 * The base class of entity instance.
 * 
 * @author wu
 *
 */



@CompileStatic
public class EntityObject {

	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
