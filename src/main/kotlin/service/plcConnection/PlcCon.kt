package service.plcConnection

import etherip.EtherNetIP
import etherip.types.CIPData
import service.IPlcConnection


class PlcCon : IPlcConnection {
    val ip = "192.168.50.58"
    val plc = EtherNetIP(ip, 0)
    override fun writeValue(item: String, value: String) {
        plc.connectTcp()
        var cipData: CIPData = plc.readTag(item)
        when (plc.readTag(item).type.name.toString()) {
            "BOOL" -> cipData.set(0, value.toInt())
            "SINT" -> cipData.set(0, value.toShort())
            "INT" -> cipData.set(0, value.toInt())
            "DINT" -> cipData.set(0, value.toInt())
            "REAL" -> cipData.set(0, value.toDouble())
            "BITS" -> cipData.set(0, value.toInt())
            "STRUCT" -> cipData.set(0, value.toInt())
            else -> throw IllegalStateException("Book is not available!")

        }
        plc.writeTag(item, cipData)
        plc.close()
    }

    override fun readValue(item: String): String {
        plc.connectTcp()
        val cipDataType = plc.readTag(item).type
        val cipDataRawString = plc.readTag(item).toString()
        val cipDataValue = cipDataRawString.substringAfter("[").substringBefore("]")
        return cipDataValue
        plc.close()
    }
}