public class Transaction {
    private Long id;
    private String productID;
    private String productName;
    private String amount;
    private String customerName;
    private Integer status;
    private String transactionDate;
    private String createBy;
    private String createOn;

    public Transaction(Long id, String productID, String productName, String amount,
                       String customerName, Integer status, String transactionDate,
                       String createBy, String createOn) {
        this.id = id;
        this.productID = productID;
        this.productName = productName;
        this.amount = amount;
        this.customerName = customerName;
        this.status = status;
        this.transactionDate = transactionDate;
        this.createBy = createBy;
        this.createOn = createOn;
    }

    public Long getId() { return id; }
    public String getProductID() { return productID; }
    public String getProductName() { return productName; }
    public String getAmount() { return amount; }
    public String getCustomerName() { return customerName; }
    public Integer getStatus() { return status; }
    public String getTransactionDate() { return transactionDate; }
    public String getCreateBy() { return createBy; }
    public String getCreateOn() { return createOn; }
}