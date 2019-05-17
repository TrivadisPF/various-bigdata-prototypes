package com.trivadis.bigdata.streamsimulator.input.excel;

import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.springframework.core.convert.converter.Converter;

import com.trivadis.bigdata.streamsimulator.input.ColumnNameProvider;
import com.trivadis.bigdata.streamsimulator.input.StringArrayToMapConverter;

/**
 * {@link Converter} to convert a {@link RowSet} into a generic Avro record schema.<br>
 * The column names are provided by the given {@link ColumnNameProvider}.
 *
 * @author Markus Zehnder
 */
public class RowSetToGenericAvroConverter implements Converter<RowSet, GenericRecord> {

    private final Schema schema;
    private final StringArrayToMapConverter converter;

    public RowSetToGenericAvroConverter(Schema schema, ColumnNameProvider<RowSet> columnNameProvider) {
        this.schema = schema;
        this.converter = new StringArrayToMapConverter(columnNameProvider);
    }

    @Override
    public GenericRecord convert(RowSet rs) {
        Map<String, String> dataMap = converter.convert(rs.getCurrentRow());

        GenericRecordBuilder builder = new GenericRecordBuilder(schema);

        for (String key : dataMap.keySet()) {
            Field field = schema.getField(key);
            if (field != null) {
                // correct type required, see org.apache.avro.generic.GenericDatumWriter called from KafkaAvroSerializer.serialize 
                switch (field.schema().getType()) {
                case INT:
                    builder.set(key, Integer.parseInt(dataMap.get(key)));
                    break;
                case LONG:
                    builder.set(key, Long.parseLong(dataMap.get(key)));
                    break;
                case FLOAT:
                    builder.set(key, Float.parseFloat(dataMap.get(key)));
                    break;
                case DOUBLE:
                    builder.set(key, Double.parseDouble(dataMap.get(key)));
                    break;
                case BOOLEAN:
                    builder.set(key, Boolean.parseBoolean(dataMap.get(key)));
                    break;
                default:
                    builder.set(key, dataMap.get(key));
                }

            }
        }

        return builder.build();
    }

}
