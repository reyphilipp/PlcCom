package service

interface IPlcConnection{
    fun readValue(item:String):String
    fun writeValue(item:String,value:String)
}