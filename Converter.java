import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Converter {
    private List<String> closingTags = new LinkedList<String>();
    private int depthLevel = 1;
    String xmlReturn = "";

    public String fileToXML(String path) throws IOException {

        xmlReturn += "<people>\n";
        
        // Read file line for line
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            stream.forEach((String row) -> {
                xmlReturn += toXml(row);
            });
        }

        xmlReturn += closeXml() + "</people>\n";

        return xmlReturn;
    }

    public String toXml(String line) {
        //Split line with delimiter "|"
        String[] values = line.split("\\|", -1);
        String xml = "";
        xml = getClosingTagsString(values[0], closingTags);

        // Numer of spaces for depth in xml
        String depth = getDepthString(depthLevel);
        xml += depth;

        //Switch case taking in tag and return a corresponding xml
        switch (values[0]) {
            case "P":
                closingTags.add("</person>");
                xml += "<person>\n" + depth + "\t<firstname>" + values[1] + "</firstname>\n" 
                        + depth + "\t<lastname>" + values[2] + "</lastname>\n";
                depthLevel++;
                return xml;

            case "F":
                closingTags.add("</family>");
                xml += "<family>\n" + depth + "\t<name>" + values[1] + "</name>\n" 
                        + depth + "\t\t<born>" + values[2] + "</born>\n";
                depthLevel++;
                return xml;

            case "T":
                xml += "<phone>\n" + depth + "\t<mobile>" + values[1] + "</mobile>\n" 
                        + depth + "\t<home>" + values[2] + "</home>\n" + depth + "</phone>\n";
                return xml;

            case "A":
                xml += "<address>\n" + depth + "\t<street>" + values[1] + "</street>\n" 
                        + depth + "\t<city>" + values[2] + "</city>\n" + depth + "</address>\n";
                return xml;
        }

        return "[Invalid tag]";
    }
    
    //Checks if a tag P or F needs to be closed, if not, nothing is returned
    private String getClosingTagsString(String currentTag, List<String> closingTags) {

        if (!currentTag.equals("T") && !currentTag.equals("A") && !closingTags.isEmpty()) {

            if (closingTags.contains("</family>")) {
                closingTags.remove("</family>");
                depthLevel--;
                // Recursive call on function in case both F and P needs to be closed
                return getDepthString(depthLevel) + "</family>\n" + 
                        getClosingTagsString(currentTag, closingTags);

            } else if (!currentTag.equals("F")) {
                closingTags.remove("</person>");
                depthLevel--;
                return getDepthString(depthLevel) + "</person>\n";
            }
        }
        return "";
    }

    // Function takes depth level and converts it into nr of spaces
    private String getDepthString(int depthLevel) {
        String tabDepth = "";
        for (int i = 0; i < depthLevel; i++) {
            tabDepth += "  ";
        }
        return tabDepth;
    }

    // Called when all lines are ran through, to close the last tag
    // Calling getClosingTagsString twitch with F and P tag to asure we get all tags closed
    // If both family and person needs to be closed, first call will return correct string and second nothing
    // If only person needs to be closed, first call will return nothing and second call will return correct string
    public String closeXml () {
        return getClosingTagsString("F", closingTags) 
                + getClosingTagsString("P", closingTags);
    }
}
