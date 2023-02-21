let fileUpload= {

	init:function(){
		document.querySelector("#uploadBtn").addEventListener("click", function(event){
			fileUpload.uploadBtn();
		});
	},


	uploadBtn:function() {
		let formData = new FormData();
		const inputTitle = document.getElementById("title").value;
		const inputFile = document.getElementById("uploadFile");
		const files = inputFile.files;

		if(inputFile.value==undefined || inputFile.value=="") {
		    alert("파일을 선택해 주십시요");
			return;
		} if(inputTitle.value == undefined || inputTitle.value == "") {
		    alert("Title을 입력해 주십시요");
		    return;
		}

		//formdata에 파일 데이터 추가
		for(let i=0; i<files.length; i++){
//			if(!checkFileName(files[i].name, files[i].size)){
//				console.log("file error");
//				return false;
//			}
			formData.append("title", inputTitle);
			formData.append("uploadVideo", files[i]);
		}

		const params={
			method:"POST",
			body:formData
		}

		fetch('/api/video/upload', params).then((response)=>response.json())
		.then((response)=>{
		    if(response.status == 200) {
		        confirm("업로드가 성공했습니다.");
		    } else if(response.status == 400) {
		        alert("업로드가 실패했습니다.");
		    }

//			if(res.resState=="success"){
//					document.querySelector("#uploadFile").value="";
//					document.querySelector("#image_container").innerHTML="<textarea>"+JSON.stringify(res, null, 4) + "</textarea>";
//					alert("업로드 처리 되었습니다.");
//			}
		}).catch((error)=>{
			console.log("error:", error);
		})

	}
}

//파일 확장자 체크 및 사이즈 체크
function checkFileName(str, fileSize){
	//1. 확장자 체크
    const ext =  str.split('.').pop().toLowerCase();
    const ableExts=['mp4'];


    if( ableExts.indexOf(ext) ==-1  ) {
        //alert(ext);
        alert(ext+' 파일은 업로드 하실 수 없습니다.');
        return false;
    }
    //2. 파일명에 특수문자 체크
    const pattern =   /[\{\}\/?,;:|*~`!^\+<>@\#$%&\\\=\'\"]/gi;
    if(pattern.test(str) ){
        //alert("파일명에 허용된 특수문자는 '-', '_', '(', ')', '[', ']', '.' 입니다.");
        alert('파일명에 특수문자를 제거해주세요.');
        return false;
    }

	const maxSize = 1024*1024*17; //517MB
	if(fileSize >= maxSize){
		alert("파일 사이즈 초과");
		return false;
	}
	return true;
}

fileUpload.init();