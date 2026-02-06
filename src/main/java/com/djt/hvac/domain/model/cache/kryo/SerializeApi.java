package com.djt.hvac.domain.model.cache.kryo;

public interface SerializeApi<R> {

  public R encode(Object t);

  public <T> T decode(R data, Class<T> type);
}
