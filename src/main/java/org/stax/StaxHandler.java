package org.stax;

import javax.xml.stream.XMLStreamException;
import java.util.function.Consumer;

/**
 * Like SAX event {@link org.xml.sax.ContentHandler handler} but for StAX.
 */
@FunctionalInterface
public interface StaxHandler {
	/**
	 * <pre>{@code
	 * <parent> <!-- sr.push(pushOrSkip("child", child); -->
	 *     <x/> <!-- skipped -->
	 *     <child> <!-- sr.push(child);
	 *         <x/> <!-- child.start(sr, "x"); -->
	 *     </child>
	 *     <y/> <!-- skipped -->
	 * </parent>
	 * }</pre>
	 * @param expected element(s) name
	 * @param child handler for element descendants
	 * @return simple handler
	 */
	static StaxHandler pushOrSkip(String expected, StaxHandler child) {
		return (sr, name) -> {
			if (expected.equals(name)) {
				sr.push(child);
			} else {
				sr.skipElement();
			}
		};
	}

	/**
	 * See {@link #stateful(StatefulInitializer, StatefulHandler, Consumer)}
	 */
	interface StatefulInitializer<S> {
		S init(StaxReader sr, String name) throws XMLStreamException;
	}
	/**
	 * See {@link #stateful(StatefulInitializer, StatefulHandler, Consumer)}
	 */
	interface StatefulHandler<S> {
		void start(S state, StaxReader sr, String name) throws XMLStreamException;
	}

	/**
	 * <pre>{@code
	 * <parent> <!-- sr.push(stateful(init, handler, end)); -->
	 *     <child> <!-- S state = init.init(sr, "child"); -->
	 *         <x/> <!-- handler.start(state, sr, "x"); -->
	 *     </child> <!-- end.accept(state); -->
	 *     <child/>
	 *     <!-- ... -->
	 * </parent>
	 * }</pre>
	 * @param init state initializer
	 * @param handler stateful handler
	 * @param end finalize state
	 * @return stateful adapter
	 */
	static <S> StaxHandler stateful(StatefulInitializer<S> init, StatefulHandler<S> handler, Consumer<S> end) {
		return (sr, name) -> {
            S state = init.init(sr, name);
            sr.push(new StaxHandler() {
                @Override
                public void start(StaxReader r, String n) throws XMLStreamException {
                    handler.start(state, r, n);
                }

                @Override
                public boolean end() {
                    end.accept(state);
                    return false;
                }
            });
        };
	}

	/**
	 * Receive notification of the start of an element.
	 *
	 * @param name XML element name
	 */
	void start(StaxReader sr, String name) throws XMLStreamException;
	/**
	 * Receive notification of the end of the element associated to this current handler (when it was {@link StaxReader#push pushed}).
	 *
	 * @return {@code true} to stop parsing
	 */
	default boolean end() {
		return false;
	}
}
