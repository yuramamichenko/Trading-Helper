package app.TradingAppCore;

public class RequestData {
    private StringBuilder data = new StringBuilder();

    public void addValues(String parameter, String value) {
        if (data.toString().length() < 1) {
            data.append("{");
        } else {
            data.append(",");
        }
        data.append("\"")
                .append(parameter)
                .append("\"")
                .append(": ")
                .append("\"")
                .append(value)
                .append("\"");
    }

    @Override
    public String toString() {
        if (data.toString().charAt(data.length() - 1) != '}') {
            data.append("}");
        }
        return data.toString();
    }
}
