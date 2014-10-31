package models

import play.api.Play.current
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Author: Dmytro Bischak
 * Date: 30.10.2014
 */
object WeatherProvider {

  private val TIMEOUT: Int = 60000

  private val SERVICE_URL = "http://www.webservicex.com/globalweather.asmx"

  def requestTemplate(cityName: String) =
    <s11:Envelope xmlns:s11="http://schemas.xmlsoap.org/soap/envelope/">
      <s11:Body>
        <ns1:GetWeather xmlns:ns1="http://www.webserviceX.NET">
          <ns1:CityName>
            {cityName}
          </ns1:CityName>
          <ns1:CountryName/>
        </ns1:GetWeather>
      </s11:Body>
    </s11:Envelope>

  def getWeatherIn(city: String): Future[Map[String, String]] = {

    val body = scala.xml.Utility.trim(requestTemplate(city))

    WS.url(SERVICE_URL)
      .withHeaders("Content-Type" -> "text/xml; charset=utf-8")
      .withRequestTimeout(TIMEOUT)
      .post(body).map(_.xml)
      .map(_ \\ "GetWeatherResult")
      .map(_.text).map(scala.xml.XML.loadString)
      .map(x => Map(
      "Location" -> (x \ "Location").text,
      "Time" -> (x \ "Time").text,
      "Temperature" -> (x \ "Temperature").text,
      "Wind" -> (x \ "Wind").text
    ))
  }

}
