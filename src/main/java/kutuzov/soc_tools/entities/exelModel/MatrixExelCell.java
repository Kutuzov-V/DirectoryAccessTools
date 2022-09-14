package kutuzov.soc_tools.entities.exelModel;

public class MatrixExelCell {
    final int rowIndex;
    final int columnIndex;
    final String access;
    final String samAccountName;



    public MatrixExelCell(int rowIndex, int columnIndex, String access, String samAccountName) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.access = access;
        this.samAccountName = samAccountName;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getAccess() {
        return access;
    }

    public String getSamAccountName() {
        return samAccountName;
    }
}
