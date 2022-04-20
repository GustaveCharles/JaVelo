package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Represents a route generator in GPX format
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public final class GpxGenerator {

    private GpxGenerator() {
    }

    /**
     * Creates a GPX file attached to a route
     *
     * @param route   the given route
     * @param profile the corresponding profile of the route
     * @return a document containing a route and its attributes,
     * namely the longitude, the latitude and the elevation for all points
     */
    public static Document createGpx(Route route, ElevationProfile profile) {
        Document doc = newDocument(); // voir plus bas

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);
        List<PointCh> points = route.points();
        double position = 0;
        for (int i = 0; i < points.size(); i++) {
            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lon", String.valueOf(Math.toDegrees(points.get(i).lon())));
            rtept.setAttribute("lat", String.valueOf(Math.toDegrees(points.get(i).lat())));
            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            if (i != points.size() - 1) {
                position += points.get(i).distanceTo(points.get(i + 1));
            }
            ele.setAttribute("altitude", String.valueOf(profile.elevationAt(position)));
        }

        return doc;
    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }

    /**
     * Writes a GPX file attached to a route
     *
     * @param name    the file name
     * @param route   the given route
     * @param profile the corresponding profile of the route
     * @throws IOException if there is an IO error
     */
    public static void writeGpx(String name, Route route, ElevationProfile profile) throws IOException {
        Document doc = createGpx(route, profile);
        Writer w = new FileWriter(name);
        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e); // Should never happen
        }
    }
}
