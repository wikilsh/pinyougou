//用户收货地址服务层
app.service('addressService',function($http){
	
	
	//获取地址列表
	this.findAddressList=function(){
		return $http.get('address/findListByLoginUser.do');
	}
	
});