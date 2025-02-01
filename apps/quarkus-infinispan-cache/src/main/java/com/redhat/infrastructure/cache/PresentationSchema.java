package com.redhat.infrastructure.cache;

import java.io.UncheckedIOException;

import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.SerializationContextInitializer;

public class PresentationSchema implements SerializationContextInitializer {

    @Override
    public String getProtoFileName() {
        return "presentation.proto";
    }

    @Override
    public String getProtoFile() throws UncheckedIOException {
        return "syntax = \"proto3\";\n" +
               "package com.redhat.domain.model;\n\n" +
               "message Presentation {\n" +
               "    int64 id = 1;\n" +
               "    string theme = 2;\n" +
               "    string author = 3;\n" +
               "    string localDate = 4;\n" +
               "}";
    }

    @Override
    public void registerSchema(SerializationContext context) {
        try {
            context.registerProtoFiles(org.infinispan.protostream.FileDescriptorSource.fromString(getProtoFileName(), getProtoFile()));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar schema ProtoBuf", e);
        }
    }

    @Override
    public void registerMarshallers(SerializationContext context) {
        try {
            context.registerMarshaller(new PresentationMarshaller());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar marshaller", e);
        }
    }
}
