package com.trivadis.bigdata.streamsimulator.input;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.springframework.core.convert.converter.Converter;

/**
 * {@link Converter} to convert a String array of record values into a generic Avro record based on the given schema.<br>
 * The column names are provided by the given {@link ColumnNameProvider}.
 * 
 * @author Markus Zehnder
 */
public class StringArrayToGenericAvroConverter extends ColumnNameAwareConverter<String[], GenericRecord> {

    private final Schema schema;

    public StringArrayToGenericAvroConverter(Schema schema) {
        this.schema = schema;
    }

    public StringArrayToGenericAvroConverter(Schema schema, ColumnNameProvider<String[]> columnNameProvider) {
        this.schema = schema;
        this.columnNameProvider = columnNameProvider;
    }

    @Override
    public GenericRecord convert(String[] record) {
        String[] header = columnNameProvider.getColumnNames();

        GenericRecordBuilder builder = new GenericRecordBuilder(schema);

        // another approach, but maybe overkill as well, would be a GenericDatumReader with self written 'map data Decoder'
        // DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
        // GenericRecord genericRecord = null;
        // genericRecord = reader.read(genericRecord, in);

        for (int i = 0; i < header.length; i++) {
            Field field = schema.getField(header[i]);

            if (field != null && record.length > i) {
                // FIXME copy paste code, also used in RowSetToGenericAvroConverter
                switch (field.schema().getType()) {
                case INT:
                    builder.set(header[i], Integer.parseInt(record[i]));
                    break;
                case LONG:
                    builder.set(header[i], Long.parseLong(record[i]));
                    break;
                case FLOAT:
                    builder.set(header[i], Float.parseFloat(record[i]));
                    break;
                case DOUBLE:
                    builder.set(header[i], Double.parseDouble(record[i]));
                    break;
                case BOOLEAN:
                    builder.set(header[i], Boolean.parseBoolean(record[i]));
                    break;
                default:
                    builder.set(header[i], record[i]);
                }
            }
        }

        return builder.build();
    }

}
