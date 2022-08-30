package wfm.shared.excel

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellAddress

// Excel Column String(A-based)을 Index(0-based) 로 변환
val String.colIndex: Int get() = CellAddress(this + "1").column

// Excel Column String(A-based)을 NumberNumber(1-based) 로 변환
val String.colNumber: Int get() = (CellAddress(this + "1").column + 1)

// Excel Row Index(0-based)를 Number(1-based) 로 변환
val Int.rowNumber: Int get() = this + 1

// Excel Column Index(0-based)를 Number(1-based) 로 변환
val Int.colNumber: Int get() = this + 1

// Excel Row Number(1-based)를 Index(0-based) 로 변환
val Int.rowIndex: Int get() = if ((this - 1) < 0) 0 else (this - 1)

// Excel Col Number(1-based)를 Index(0-based) 로 변환
val Int.colIndex: Int get() = if ((this - 1) < 0) 0 else (this - 1)

// row(1-based), col(A-based)
fun Sheet.cellAt(row: Int, col: String): Cell? = getRow(row.rowIndex)?.getCell(col.colIndex)

// row(0-based), col(0-based)
fun Sheet.cellAtIndex(rowIndex: Int, colIndex: Int): Cell? = getRow(rowIndex)?.getCell(colIndex)

fun Sheet.createCellAtIndex(rowIndex: Int, colIndex: Int): Cell? = getRow(rowIndex).createCell(colIndex)

// address(A1-based)
fun Sheet.cellAtAddress(address: String): Cell? = CellAddress(address).run { getRow(row)?.getCell(column) }

val Sheet.lastRowNumber: Int get() = lastRowNum + 1

val Row.lastColNumber: Int get() = lastCellNum + 1
