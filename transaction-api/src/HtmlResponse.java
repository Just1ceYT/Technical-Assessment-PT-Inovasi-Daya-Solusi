import java.util.List;
import java.util.Map;

public class HtmlResponse {
    public static String createHtmlTable(List<Transaction> transactions, Map<Integer, String> statusMap) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>")
                .append("<html lang='en'>")
                .append("<head>")
                .append("<meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>Transaction Data</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }")
                .append("h1 { color: #333; text-align: center; }")
                .append(".container { max-width: 1200px; margin: 0 auto; }")
                .append("table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }")
                .append("th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; }")
                .append("th { background-color: #4CAF50; color: white; font-weight: bold; position: sticky; top: 0; }")
                .append("tr:hover { background-color: #f5f5f5; }")
                .append(".success { color: green; font-weight: bold; }")
                .append(".failed { color: red; font-weight: bold; }")
                .append(".summary { background: white; padding: 15px; margin-bottom: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }")
                .append(".original-id { background-color: #e8f5e8; font-weight: bold; }")
                .append(".null-value { color: #888; font-style: italic; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<h1>📊 Transaction Data (From JSON File)</h1>");

        // Summary section
        html.append("<div class='summary'>")
                .append("<h3>Summary</h3>")
                .append("<p>Total Transactions: ").append(transactions.size()).append("</p>");

        // Calculate success/failed counts using actual status names from database
        long successCount = transactions.stream()
                .filter(t -> statusMap.containsKey(t.getStatus()) && "SUCCESS".equals(statusMap.get(t.getStatus())))
                .count();
        long failedCount = transactions.stream()
                .filter(t -> statusMap.containsKey(t.getStatus()) && "FAILED".equals(statusMap.get(t.getStatus())))
                .count();

        html.append("<p>Successful: <span class='success'>").append(successCount).append("</span></p>")
                .append("<p>Failed: <span class='failed'>").append(failedCount).append("</span></p>")
                .append("<p><small>Data loaded from: viewData.json | Status from: Database</small></p>")
                .append("</div>");

        // Transactions table
        html.append("<table>")
                .append("<thead>")
                .append("<tr>")
                .append("<th>Original ID</th>")
                .append("<th>Product ID</th>")
                .append("<th>Product Name</th>")
                .append("<th>Amount</th>")
                .append("<th>Customer</th>")
                .append("<th>Status</th>")
                .append("<th>Transaction Date</th>")
                .append("<th>Created By</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        for (Transaction t : transactions) {
            // Get status name from database, not hardcoded logic
            String statusName = statusMap.get(t.getStatus());
            String statusClass = "SUCCESS".equals(statusName) ? "success" :
                    "FAILED".equals(statusName) ? "failed" : "";

            html.append("<tr>")
                    .append("<td class='original-id'>").append(t.getId()).append("</td>")
                    .append("<td>").append(escapeHtml(t.getProductID())).append("</td>")
                    .append("<td>").append(escapeHtml(t.getProductName())).append("</td>")
                    .append("<td>$").append(escapeHtml(t.getAmount())).append("</td>")
                    .append("<td>").append(formatValue(t.getCustomerName())).append("</td>")
                    .append("<td class='").append(statusClass).append("'>")
                    .append(statusName != null ? statusName : "Unknown (" + t.getStatus() + ")")
                    .append("</td>")
                    .append("<td>").append(formatValue(t.getTransactionDate())).append("</td>")
                    .append("<td>").append(formatValue(t.getCreateBy())).append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody>")
                .append("</table>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        return html.toString();
    }

    private static String formatValue(String value) {
        if (value == null || value.isEmpty()) {
            return "<span class='null-value'>N/A</span>";
        }
        return escapeHtml(value);
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}