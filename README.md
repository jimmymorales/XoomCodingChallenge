# XoomCodingChallenge

## Instructions
You can see the instructions in this [pdf file](https://github.com/jimmymorales/XoomCodingChallenge/blob/master/Xoom%20Code%20Challenge%20-%20Senior%20Software%20Engineer%2C%20Android.pdf).

## Solution
The app was written using Kotlin and latest version of Android Jetpack libraries.

This solution uses a [Repository][1] that will use the local database to page in data for the UI and also back-fill the database from the network as the user reaches to the end of the data in the database.

It uses Room to create the DataSource.Factory ([dao][2]) and the Paging Boundary Callback API to get notified when the Paging library consumes the available local data ([boundary callback implementation][3])

This usually provides the best user experience as the cached content is always available on the device and the user will still have a good experience even if the network is slow / unavailable.

## Libraries
* [Android Support Library][support-lib]
* [Android Architecture Components][arch]
* [Retrofit][retrofit] for REST api communication
* [Glide][glide] for image loading
* [espresso][espresso] for UI tests
* [mockito][mockito] for mocking in tests
* [Retrofit Mock][retrofit-mock] for creating a fake API implementation for tests

[1]: https://github.com/jimmymorales/XoomCodingChallenge/blob/master/app/src/main/java/com/jmlabs/xoomcodechallenge/repository/XoomCountriesRepository.kt
[2]: https://github.com/jimmymorales/XoomCodingChallenge/blob/master/app/src/main/java/com/jmlabs/xoomcodechallenge/db/XoomCountryDao.kt
[3]: https://github.com/jimmymorales/XoomCodingChallenge/blob/master/app/src/main/java/com/jmlabs/xoomcodechallenge/repository/CountriesBoundaryCallback.kt
[mockwebserver]: https://github.com/square/okhttp/tree/master/mockwebserver
[support-lib]: https://developer.android.com/topic/libraries/support-library/index.html
[arch]: https://developer.android.com/arch
[espresso]: https://google.github.io/android-testing-support-library/docs/espresso/
[retrofit]: http://square.github.io/retrofit
[glide]: https://github.com/bumptech/glide
[mockito]: http://site.mockito.org
[retrofit-mock]: https://github.com/square/retrofit/tree/master/retrofit-mock
