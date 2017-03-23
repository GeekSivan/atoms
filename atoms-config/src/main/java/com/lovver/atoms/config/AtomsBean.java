package com.lovver.atoms.config;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("atoms")
public class AtomsBean {

	private AtomsBroadCastBean broadcast;

	private AtomsSerializerBean serializer;

	@XStreamAsAttribute
	private String application="atoms";
	
	@XStreamImplicit(itemFieldName = "cache")  
	private List<AtomsCacheBean> cache;

	public AtomsBroadCastBean getBroadcast() {
		return broadcast;
	}

	public void setBroadcast(AtomsBroadCastBean broadcast) {
		this.broadcast = broadcast;
	}

	public AtomsSerializerBean getSerializer() {
		return serializer;
	}

	public void setSerializer(AtomsSerializerBean serializer) {
		this.serializer = serializer;
	}

	public List<AtomsCacheBean> getCache() {
		return cache;
	}

	public void setCache(List<AtomsCacheBean> cache) {
		this.cache = cache;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
