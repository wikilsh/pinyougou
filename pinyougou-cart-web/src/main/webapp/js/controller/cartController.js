app.controller('cartController', function($scope, cartService) {
	// 购物车列表
	$scope.findCartList = function() {
		cartService.findCartList().success(function(response) {
			$scope.cartList = response;
			//求合计数
			$scope.totalValue=cartService.sum($scope.cartList);
		});
	}
	// 數量加減
	$scope.addGoodsToCartList = function(itemId, num) {
		cartService.addGoodsToCartList(itemId, num).success(function(response) {
			if (response.success) {
				$scope.findCartList();// 刷新列表
			} else {
				alert(response.message);// 弹出错误提示
			}
		});
	}
	
	//获取地址列表
	$scope.findAddressList=function(){
		cartService.findAddressList().success(
			function(response){
				$scope.addressList=response;
				//设置默认地址
				for(var i=0;i< $scope.addressList.length;i++){ 
					if($scope.addressList[i].isDefault=='1'){ 
						$scope.address=$scope.addressList[i]; 
						break; 
					}
				}
			}
				
		);
	}
	
	//选择地址
	$scope.selectAddress=function(address){
		$scope.address=address;
	}
	
	//判断是否是当前选中的地址
	$scope.isSelectedAddress=function(address){
		if(address==$scope.address){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.order={paymentType:'1'};
	//选择支付方式
	$scope.selectPayType=function(type){
		$scope.order.paymentType=type;
	}
	
});