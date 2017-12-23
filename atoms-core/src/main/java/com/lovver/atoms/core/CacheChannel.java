package com.lovver.atoms.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.lovver.atoms.cache.Cache;
import com.lovver.atoms.cache.CacheProvider;
import com.lovver.atoms.common.exception.CacheException;
import com.lovver.atoms.config.AtomsCacheBean;
import com.lovver.atoms.config.AtomsConfig;
import com.lovver.atoms.context.AtomsContext;

public class CacheChannel {
	private static CacheChannel instance=new CacheChannel();

    private Map<String,CacheProvider> mCacheProvider=AtomsContext.getCacheProvider();

	public static CacheChannel getInstance(){
		return instance;
	}
	
	
	private Object[] getNextLevelCache(String region,Object key){
		Object[] value=null;
		Map<String,CacheProvider> mCacheProvider=AtomsContext.getCacheProvider();
		for(int i=2;i<=mCacheProvider.size();i++){
			
			Cache cache=AtomsContext.getCache(region, i);
			value=new Object[]{
					i,cache.get(key)
			};
			if(value!=null){
				break;
			}
		}
		
		return value;
	}
	

	/**
	 * 获取缓存中的数据
	 * 
	 * @param region
	 *            : Cache Region name
	 * @param key
	 *            : Cache key
	 * @return cache object
	 */
	public Object get(String region, Object key) {
		
		if (region != null && key != null) {
			Cache tCache=CacheManager.getCache(1, region, true);
			Object value=tCache.get(key);
			if(value==null){
				Object[] levelValue=getNextLevelCache(region,key);
				if(levelValue!=null){
					value=levelValue[1];
				}
				tCache.put(key,value);
			}
			return value;
		}
		return null;
	}

	/**
	 * 写入缓存
	 * 
	 * @param region
	 *            : Cache Region name
	 * @param key
	 *            : Cache key
	 * @param value
	 *            : Cache value
	 */
	public void set(String region, Object key, Object value) {
//		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa["+region+"]["+key+"]=="+ JSON.toJSONString(value));
		if (region != null && key != null) {
			if (value == null) {
                evict(region, key);
            } else {
                for(int i=mCacheProvider.size();i>0;i--) {
                    Cache cache = CacheManager.getCache(i, region, true);
					cache.evict(key);
                    cache.put(key, value);
                }
			}
		}
	}

	public void set(String region, Object key, Object value,Integer expiretime) {
		if (region != null && key != null) {
			if (value == null) {
                evict(region, key);
            } else {
				for(int i=mCacheProvider.size();i>0;i--) {
					Cache cache = CacheManager.getCache(i, region, true);
					cache.evict(key);
					cache.put(key, value,expiretime);
				}
			}
		}
	}

	/**
	 * 删除缓存
	 * 
	 * @param region
	 *            : Cache Region name
	 * @param key
	 *            : Cache key
	 */
	public void evict(String region, Object key) {
        for(int i=1;i<=mCacheProvider.size();i++) {
            Cache cache=CacheManager.getCache(i,region,true);
            cache.evict(key);
        }
	}

	/**
	 * 批量删除缓存
	 * 
	 * @param region
	 *            : Cache region name
	 * @param keys
	 *            : Cache key
	 */
	@SuppressWarnings({ "rawtypes" })
	public void batchEvict(String region, List keys) {
		for(int i=mCacheProvider.size();i>0;i--) {
            Cache cache = CacheManager.getCache(i, region, true);
            cache.evict(keys);
        }
	}

	/**
	 * Clear the cache
	 * 
	 * @param region
	 *            : Cache region name
	 */
	public void clear(String region) throws CacheException {
		for(int i=mCacheProvider.size();i>0;i--) {
            Cache cache = CacheManager.getCache(i, region, true);
            cache.clear();
        }
	}

	/**
	 * Get cache region keys
	 * 
	 * @param region
	 *            : Cache region name
	 * @return key list
	 */
	@SuppressWarnings("rawtypes")
	public List keys(String region) throws CacheException {
		List lstRet=null;
        Cache cache = CacheManager.getCache(1, region, true);
        lstRet = cache.keys();
		return lstRet;
	}

	

	/**
	 * 关闭到通道的连接
	 */
	public void close() {
		Collection<AtomsCacheBean> lstCacheBean=AtomsConfig.getAtomsConfig().getCache();
		for(AtomsCacheBean cp:lstCacheBean){
			CacheManager.shutdown(Integer.parseInt(cp.getLevel()));
		}
		
	}
}