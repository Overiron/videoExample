let fileSearch= {

	init:function(){
		document.querySelector("#searchBtn").addEventListener("click", function(event){
			fileSearch.searchBtn();
		});
		document.querySelector("#progressBtn").addEventListener("click", function(event){
        	fileSearch.progressBtn();
        });
	},

	searchBtn:function() {
	    let formData = new FormData();
    	const videoId = document.getElementById("videoId").value;
        console.log("search videoId ==== " + videoId);
    	formData.append("videoId", videoId);
        var url = '/api/video/search/'+videoId;

        fetch(url)
        .then((response) => response.json())
        .then((json) => console.log(json));
	},

	progressBtn:function() {
    	let formData = new FormData();
        const videoId = document.getElementById("videoId").value;
        console.log("progress videoId ==== " + videoId);
        formData.append("videoId", videoId);
        var url = '/api/video/progress/'+videoId;

        fetch(url)
        .then((progressResponse) => progressResponse.json())
        .then((json) => console.log(json));
    }
}

fileSearch.init();