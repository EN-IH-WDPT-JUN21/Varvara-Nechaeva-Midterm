package com.ironhack.midterm.dao.account;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class CustomAccountSerializer extends StdSerializer<Account> {

  public CustomAccountSerializer() {
    this(null);
  }

  public CustomAccountSerializer(Class<Account> t) {
    super(t);
  }

  @Override
  public void serialize(
      Account account, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeString(account.getId().toString());
  }
}
