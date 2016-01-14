package countryregister

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val uri1="http://country.preview.openregister.org"
//	val uri1="http://d21k0u23fx1r6s.cloudfront.net"

	val httpProtocol = http
		.baseURL(uri1)
		.inferHtmlResources()
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:33.0) Gecko/20100101 Firefox/33.0")

	val headers_1 = Map("Accept" -> "image/png,image/*;q=0.8,*/*;q=0.5")



	val scn = scenario("RecordedSimulation")
		.exec(http("HomePage")
			.get("/")
			.resources(http("Asset_CrownlogoImage")
			.get(uri1 + "/assets/images/gov.uk_logotype_crown.png")
			.headers(headers_1)
			.check(status.is(404))))
		.pause(1)
		.exec(http("RecordsPage")
			.get("/records")
			.resources(http("Asset_CrownlogoImage")
			.get(uri1 + "/assets/images/gov.uk_logotype_crown.png")
			.headers(headers_1)
			.check(status.is(404))))
		.pause(7)
		.exec(http("ZM_CountryRecord")
			.get("/country/ZM")
			.resources(http("Asset_CrownlogoImage")
			.get(uri1 + "/assets/images/gov.uk_logotype_crown.png")
			.headers(headers_1)
			.check(status.is(404))))
		.pause(2)
		.exec(http("ZM_CountryRecordJson")
			.get("/country/ZM.json"))
		.pause(6)
		.exec(http("EntriesPage")
			.get("/entries")
			.resources(http("Asset_CrownlogoImage")
			.get(uri1 + "/assets/images/gov.uk_logotype_crown.png")
			.headers(headers_1)
			.check(status.is(404))))
		.pause(1)
		.exec(http("Entry197")
			.get("/entry/197")
			.resources(http("Asset_CrownlogoImage")
			.get(uri1 + "/assets/images/gov.uk_logotype_crown.png")
			.headers(headers_1)
			.check(status.is(404))))
		.pause(2)
		.exec(http("Entry197Json")
			.get("/entry/197.json"))

//	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	setUp(scn.inject(rampUsers(10) over (1 seconds))).protocols(httpProtocol)
}
