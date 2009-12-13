/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.sax;

import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * Unit tests for the {@link XHTMLContentHandler} class.
 */
public class XHTMLContentHandlerTest extends TestCase {

    private ContentHandler output;

    private XHTMLContentHandler xhtml;

    protected void setUp() {
        output = new BodyContentHandler();
        xhtml = new XHTMLContentHandler(output, new Metadata());
    }

    /**
     * Test that content in block elements are properly separated in text
     * output.
     *
     * @see <a href="https://issues.apache.org/jira/browse/TIKA-188">TIKA-188</a>
     */
    public void testExtraWhitespace() throws SAXException {
        xhtml.startDocument();

        xhtml.element("p", "foo");
        xhtml.startElement("p");
        xhtml.characters("b");
        xhtml.element("b", "a"); // inlines should not cause extra whitespace
        xhtml.characters("r");
        xhtml.endElement("p");

        xhtml.startElement("table");
        xhtml.startElement("tr");
        xhtml.element("th", "x");
        xhtml.element("th", "y");
        xhtml.endElement("tr");
        xhtml.startElement("tr");
        xhtml.element("td", "a");
        xhtml.element("td", "b");
        xhtml.endElement("tr");
        xhtml.endElement("table");
        xhtml.endDocument();

        System.out.println(output);
        String[] words = output.toString().split("\\s+");
        assertEquals(6, words.length);
        assertEquals("foo", words[0]);
        assertEquals("bar", words[1]);
        assertEquals("x", words[2]);
        assertEquals("y", words[3]);
        assertEquals("a", words[4]);
        assertEquals("b", words[5]);
    }

}
