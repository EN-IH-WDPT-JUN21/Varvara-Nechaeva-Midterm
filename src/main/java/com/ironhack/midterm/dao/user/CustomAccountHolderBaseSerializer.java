package com.ironhack.midterm.dao.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.ironhack.midterm.dao.user.AccountHolderBase;

import java.io.IOException;

public class CustomAccountHolderBaseSerializer extends StdSerializer<AccountHolderBase> {
  public CustomAccountHolderBaseSerializer() {
    this(null);
  }

  public CustomAccountHolderBaseSerializer(Class<AccountHolderBase> t) {
    super(t);
  }

  @Override
  public void serialize(
      AccountHolderBase accountHolderBase,
      JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeString(accountHolderBase.getId().toString());
  }
}
