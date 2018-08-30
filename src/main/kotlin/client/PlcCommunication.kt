package client

import service.IPlcConnection

class PlcCommunication(val iPlcConnection: IPlcConnection){
    fun checkRain(){
        when (iPlcConnection.readValue("rain")) {
            "true" -> println("It's rainig")
            "false" -> println("It's dry")
            else -> throw IllegalStateException("Book is not available!")
        }
    }
}