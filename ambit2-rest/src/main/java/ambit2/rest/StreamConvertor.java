package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.RepresentationConvertor;

public class StreamConvertor<T, Q, R extends Reporter<Q, OutputStream>>
		extends RepresentationConvertor<T, Q, OutputStream, R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1652253598534818669L;

	public StreamConvertor(R reporter, MediaType mediaType) {
		this(reporter, mediaType, null);
	}

	public StreamConvertor(R reporter, MediaType mediaType, String fileNamePrefix) {
		super(reporter, mediaType, fileNamePrefix);
	}

	@Override
	public Representation process(final Q query) throws Exception {
		try {
			Representation rep = new OutputRepresentation(mediaType) {
				@Override
				public void write(OutputStream stream) throws IOException {

					try {

						getReporter().setOutput(stream);
						getReporter().process(query);
						// writer.flush();
						// stream.flush();
					} catch (NotFoundException x) {
						;
					} catch (Exception x) {
						Throwable ex = x;
						while (ex != null) {
							if (ex instanceof IOException)
								throw (IOException) ex;
							ex = ex.getCause();
						}
						Context.getCurrentLogger().warning(x.getMessage() == null ? x.toString() : x.getMessage());

					} finally {

						try {
							if (stream != null)
								stream.flush();
						} catch (Exception x) {
							x.printStackTrace();
						}
						try {
							getReporter().close();
						} catch (Exception x) {
							x.printStackTrace();
						}
					}
				}
			};
			setDisposition(rep);
			return rep;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			try {
				reporter.close();
			} catch (Exception x) {
			}
		}
	}
}