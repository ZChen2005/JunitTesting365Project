import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GUI extends JFrame {
    private JTextField inputField;
    private JButton searchButton;
    private JTextArea resultArea;

    private List<HT> FT;
    private List<String> urls;

    public GUI(List<HT> FT, List<String> urls){
        this.FT = FT;
        this.urls = urls;

        setSize(600,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Input topic:");
        inputField = new JTextField(20);
        searchButton = new JButton("Search");

        top.add(label);
        top.add(inputField);
        top.add(searchButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String query = inputField.getText().trim().toLowerCase();

            if (query.isEmpty()) {
                resultArea.setText("Please enter a topic.");
                return;
            }

            // check if search is in your urls
            boolean found = false;
            for (String url : urls) {
                if (url.toLowerCase().contains(query)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                resultArea.setText("Topic not found in the dataset. Try a different word.");
                return;
            }

            try {
                Search();
            } catch (IOException ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });

    }
    private void Search() throws IOException {
        String query = inputField.getText().trim();
        if (query.isEmpty()) {
            resultArea.setText("Please enter a topic.");
            return;
        }

        HT queryDoc = Main.convert("https://en.wikipedia.org/wiki/" + query);
        Main.TF(queryDoc);
        Main.IDF(queryDoc, FT);


        double max1 = -1, max2 = -1;
        int idx1 = -1, idx2 = -1;

        String queryUrl = "https://en.wikipedia.org/wiki/" + query;

        for (int i = 0; i < FT.size(); i++) {

            String candidateUrl = urls.get(i);

            // skips searched topic
            if (candidateUrl.equalsIgnoreCase(queryUrl)) continue;

            // find two similar
            double sim = Main.cosineSimilarity(queryDoc, FT.get(i));
            if (sim > max1) {
                max2 = max1; idx2 = idx1;
                max1 = sim; idx1 = i;
            } else if (sim > max2) {
                max2 = sim; idx2 = i;
            }
        }

        resultArea.setText("Top matches for '" + query + "':\n");
        if (idx1 != -1) resultArea.append("1. " + urls.get(idx1) + " (sim=" + max1 + ")\n");
        if (idx2 != -1) resultArea.append("2. " + urls.get(idx2) + " (sim=" + max2 + ")\n");
    }
}
