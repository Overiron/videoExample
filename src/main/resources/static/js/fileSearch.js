let fileSearch= {

	init:function(){
		document.querySelector("#searchBtn").addEventListener("click", function(event){
			fileSearch.searchBtn();
		});
		console.log("fileSearch");
	},

	searchBtn:function() {
	    let formData = new FormData();
    	const videoId = document.getElementById("videoId").value;
        console.log("videoId ==== " + videoId);
    	formData.append("videoId", videoId);

    	const params={
        	method:"POST",
        	body:formData
        }

        fetch('/api/video/search', params).then((response)=>response.json).then(data => console.log(data));
	}
}

fileSearch.init();