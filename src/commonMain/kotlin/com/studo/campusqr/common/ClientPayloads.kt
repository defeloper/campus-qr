package com.studo.campusqr.common


interface ClientPayload

class ClientLocation(
    val id: String,
    val name: String,
    val checkInCount: Int,
    val accessType: LocationAccessType
) : ClientPayload

class UserData : ClientPayload {
  lateinit var appName: String
  var clientUser: ClientUser? = null // Null when unauthenticated
  var externalAuthProvider: Boolean = false
}

val UserData.isAuthenticated get() = clientUser != null

class ReportData(
  val impactedUsersCount: Int,
  val impactedUsersMailtoLink: String,
  val impactedUsersEmailsCsvData: String,
  val reportedUserLocations: Array<UserLocation>,
  val reportedUserLocationsCsv: String,
  val reportedUserLocationsCsvFileName: String,
  val startDate: String,
  val endDate: String,
  val impactedUsersEmailsCsvFileName: String
) : ClientPayload {
  class UserLocation(val email: String, val date: String, val locationName: String)
}

class LocationVisitData(
  val csvData: String,
  val csvFileName: String
) : ClientPayload

class ClientUser(
  val id: String,
  val email: String,
  val name: String,
  val type: String,
  val firstLoginDate: String
) : ClientPayload

class ClientAccessManagement(
  val id: String,
  val locationName: String,
  val allowedEmails: Array<String>,
  val dateRanges: Array<ClientDateRange>,
  val note: String,
  val reason: String
) : ClientPayload

class ClientDateRange(
  val from: Double,
  val to: Double
) : ClientPayload

class NewAccess(
  val locationId: String,
  val allowedEmails: Array<String>,
  val dateRanges: Array<ClientDateRange>,
  val note: String,
  val reason: String
) : ClientPayload

class EditAccess(
  val locationId: String? = null,
  val allowedEmails: Array<String>? = null,
  val dateRanges: Array<ClientDateRange>? = null,
  val note: String? = null,
  val reason: String? = null
) : ClientPayload