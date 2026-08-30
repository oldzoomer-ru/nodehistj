val port = 8082

GET("http://localhost:$port/diff/history") {

}
GET("http://localhost:$port/diff/history?zone=2") {

}
GET("http://localhost:$port/diff/history?zone=2&network=5015") {

}
GET("http://localhost:$port/diff/history?zone=2&network=5015&node=519") {

}
GET("http://localhost:$port/diff/history?zone=2&page=1&size=10") {

}
GET("http://localhost:$port/diff/history?size=50") {

}
GET("http://localhost:$port/diff/history?zone=50000") {

}
GET("http://localhost:$port/diff/history?zone=999&page=0&size=20") {

}
