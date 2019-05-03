app.service("uploadService",function($http){
	
	this.uploadFile=function(){
		var formData=new FormData();//H5新增的类
		formData.append("file",file.files[0]);//file:文件上传框的name  
		return $http({
			method:'POST',
			url:"../upload.do",
			data:formData,
			headers:{'Content-Type':undefined},  //默认值是json，设置为multipart
			transformRequest:angular.indentity   //对表单进行二进制序列化
		});
	}
	
	
	
	
	
});