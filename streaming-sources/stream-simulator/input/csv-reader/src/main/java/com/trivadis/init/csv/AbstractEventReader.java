package com.trivadis.init.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.trivadis.init.csv.event.AbstractEvent;
import com.trivadis.init.csv.handler.CSVRecordHandler;


public abstract class AbstractEventReader implements EventReader {
	private List<CSVRecordHandler> handlers = new ArrayList<CSVRecordHandler>();
	private CSVReader reader;
	
	private AbstractEvent currentEvent;

	public AbstractEventReader(String eventFile, int skipLines) throws FileNotFoundException {
		CSVParser parser =
				new CSVParserBuilder()
				.withSeparator(';')
				.withIgnoreQuotations(true)
				.build();
		
		this.reader =
				new CSVReaderBuilder(new FileReader(ResourceUtils.getFile(eventFile)))
				.withSkipLines(skipLines)
				.withCSVParser(parser)
				.build();
	}
	
	/* (non-Javadoc)
	 * @see com.trivadis.init.csv.EventReader#hasNext()
	 */
	public boolean hasNext() throws IOException {
		String[] record = reader.readNext();
		if (record == null) {
			return false;
		}

		for (CSVRecordHandler handler : handlers) {
			if (handler.canHandle(record)) {
				currentEvent = handler.handle(record);
				break;
			}
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see com.trivadis.init.csv.EventReader#getEventTime()
	 */
	public Integer getEventTime() {
		if (currentEvent != null) {
			return currentEvent.getEventTimestamp();
		} else {
			return Integer.MIN_VALUE;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.trivadis.init.csv.EventReader#getMessage()
	 */
	public AbstractEvent getMessage() {
		return currentEvent;
	}
	
	/**
	 * Registers a handler for processing input of a given label
	 * @param handler
	 */
	public void registerHandler(CSVRecordHandler handler) {
		Assert.isTrue(!handlers.contains(handler), "There is already a handler registered for label=" + handler.getClass().getName());
		this.handlers.add(handler);
	}

}
