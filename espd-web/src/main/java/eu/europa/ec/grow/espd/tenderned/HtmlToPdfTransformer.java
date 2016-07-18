package eu.europa.ec.grow.espd.tenderned;

import eu.europa.ec.grow.espd.tenderned.exception.PdfRenderingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HtmlToPdfTransformer {

    public final static String XSL_CA = "xhtml2fo_tenderned_ca.xsl";
    public final static String XSL_EO = "xhtml2fo_tenderned_eo.xsl";

    /**
     * Method that will convert the given XML to PDF
     * @param html is a String of html content
     * @throws PdfRenderingException
     */
    public byte[] convertToPDF(String html, String agent) throws TransformerException, IOException, FOPException, URISyntaxException, PdfRenderingException {
        String xsl;
        if ("ca".equals(agent)) {
            xsl = XSL_CA;
        } else {
            xsl = XSL_EO;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream htmlInputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
        InputStreamReader htmlInputStreamReader = new InputStreamReader(htmlInputStream, UTF_8);
        StreamSource xhtmlSource = new StreamSource(htmlInputStreamReader);

        final XsltURIResolver resolver = new XsltURIResolver();
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setURIResolver(resolver);

        final Source xslSource = resolver.resolve(xsl, null);
        final URI fopConfigURI = this.getClass().getResource("/tenderned/pdfrendering/fop/fop-config.xml").toURI();
        final Transformer xhtml2foTransformer = transformerFactory.newTransformer(xslSource);
        final FopFactory fopFactory = FopFactory.newInstance(fopConfigURI);
        final Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
        final Result res = new SAXResult(fop.getDefaultHandler());
        xhtml2foTransformer.transform(xhtmlSource, res);
        out.close();
        return out.toByteArray();
    }

    private static class XsltURIResolver implements URIResolver {

        @Override
        public Source resolve(String href, String base) throws TransformerException {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("tenderned/pdfrendering/xslt/" + href);
            return new StreamSource(inputStream);
        }
    }

    //Tijdelijke code om html op te slaan
    public static void saveHtml(String html) throws IOException {
        File htmlFile = new File("/htmlprintpage.xhtml");
        System.out.println("htmlFile path = " + htmlFile.getAbsolutePath());
        byte[] htmlByteArray = html.getBytes(UTF_8);
        FileUtils.writeByteArrayToFile(htmlFile, htmlByteArray);
    }

}
