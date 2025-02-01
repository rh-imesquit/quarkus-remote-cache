package com.redhat.infrastructure.cache;

import java.io.IOException;
import java.time.LocalDateTime;

import org.infinispan.protostream.MessageMarshaller;

import com.redhat.domain.model.Presentation;

public class PresentationMarshaller implements MessageMarshaller<Presentation> {

    @Override
    public String getTypeName() {
        return "com.redhat.domain.model.Presentation";
    }

    @Override
    public Class<Presentation> getJavaClass() {
        return Presentation.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Presentation presentation) throws IOException {
        writer.writeLong("id", presentation.getId());
        writer.writeString("theme", presentation.getTheme());
        writer.writeString("author", presentation.getAuthor());
        writer.writeString("localDate", presentation.getDateTime().toString());
    }

    @Override
    public Presentation readFrom(ProtoStreamReader reader) throws IOException {
        Presentation presentation = new Presentation();
        presentation.setId(reader.readLong("id"));
        presentation.setTheme(reader.readString("theme"));
        presentation.setAuthor(reader.readString("author"));
        presentation.setDateTime(LocalDateTime.parse(reader.readString("localDate")));
        return presentation;
    }
}
