var N = 2000;
for(var i = 0; i < N; i++){
	var x = Math.random();
	var j;
	if ( x < 0.33){
		j = "job1";
	} else if ( x < 0.66){
		j = "job2";
	} else {
		j = "job3";
	}
	var t = parseInt(Math.random() * 100);
	console.log(j,t);
}
