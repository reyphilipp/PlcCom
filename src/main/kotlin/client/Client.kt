import service.plcConnection.PlcCon


fun main(args: Array<String>) {
    var plcCon = PlcCon()

    println(plcCon.readValue("DateTime[1]"))
    println(plcCon.readValue("DateTime[2]"))
    println(plcCon.readValue("DateTime[3]"))
    println(plcCon.readValue("DateTime[4]"))
    println(plcCon.readValue("DateTime[5]"))
    println(plcCon.readValue("DateTime[6]"))
    plcCon.writeValue("Rasp_Temp_Java[3]","11.285")
}