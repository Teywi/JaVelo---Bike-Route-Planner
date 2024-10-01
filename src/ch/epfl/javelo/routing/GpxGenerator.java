package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * GpxGenerator generates the gpx files
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public class GpxGenerator {

    private GpxGenerator(){}

    /**
     * Gpx document from a route and an elevation profile
     *
     * @param route route
     * @param profile elevations of the route
     * @return Gpx document from a given route and a given elevation profile
     */
    public static Document createGpx(Route route, ElevationProfile profile){
        Document doc = newDocument();

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

        double currentPos = 0;

        //create the elements for each point on the route
        for (PointCh p : route.points()){

            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lat", String.valueOf(Math.toDegrees(p.lat())));
            rtept.setAttribute("lon", String.valueOf(Math.toDegrees(p.lon())));

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);

            ele.setTextContent(String.valueOf(profile.elevationAt(currentPos)));

            currentPos += profile.length()/(double)route.points().size();
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
     * Write the Gpx file
     *
     * @param nameFile name of the file containing the Gpx document
     * @param route route
     * @param profile elevations of the route
     * @throws IOException if there was an error during the writing of the document
     */
    public static void writeGpx(String nameFile, Route route, ElevationProfile profile) throws IOException {
        Document doc = createGpx(route, profile);
        Writer w = new FileWriter(nameFile);

        try{
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(w));
        } catch (TransformerException t){
            throw new Error(t); //should never happen
        }
    }
}
