package client


import org.junit.Test
import service.IPlcConnection
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever

class PlcCommunicationTest{

    @Test
    fun should_CallRainMethod_when_IsRaining(){
        //Triple A
        // Arrange
        val iPlcConnection : IPlcConnection = mock()
        whenever(iPlcConnection.readValue("rain")).thenReturn("true")
        val sut = PlcCommunication(iPlcConnection)

        // Act
        sut.checkRain()

        // Assert
        verify(iPlcConnection).readValue("rain")
    }

    @Test
    fun should_CallRainMethod_when_IsNotRaining(){
        //Triple A
        // Arrange
        val iPlcConnection : IPlcConnection = mock()
        whenever(iPlcConnection.readValue("rain")).thenReturn("false")
        val sut = PlcCommunication(iPlcConnection)

        // Act
        sut.checkRain()

        // Assert
        verify(iPlcConnection).readValue("rain")
    }

    @Test(expected = IllegalStateException::class)
    fun should_ThrowIllegalStateException_when_BookIsNotAvailable(){
        //Triple A
        // Arrange
        val iPlcConnection : IPlcConnection = mock()
        whenever(iPlcConnection.readValue("rain")).thenReturn("Error")
        val sut = PlcCommunication(iPlcConnection)

        // Act
        sut.checkRain()

        // Assert

    }
}