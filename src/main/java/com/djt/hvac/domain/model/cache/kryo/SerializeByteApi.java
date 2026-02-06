package com.djt.hvac.domain.model.cache.kryo;

public interface SerializeByteApi extends SerializeApi<byte[]> {

  @Override
  public byte[] encode(Object t);

  @Override
  public <T> T decode(byte[] data, Class<T> type);
}
