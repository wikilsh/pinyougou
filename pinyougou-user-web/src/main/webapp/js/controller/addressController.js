app.controller('addressController', function($scope, addressService) {
	
	// 获取地址列表
	$scope.findAddressList = function() {
		addressService.findAddressList().success(function(response) {
			$scope.addressList = response;
			// 设置默认地址
			for (var i = 0; i < $scope.addressList.length; i++) {
				if ($scope.addressList[i].isDefault == '1') {
					$scope.address = $scope.addressList[i];
					break;
				}
			}
		}

		);
	}

	// 选择地址
	$scope.selectAddress = function(address) {
		$scope.address = address;
	}

	// 判断是否是当前选中的地址
	$scope.isSelectedAddress = function(address) {
		if (address == $scope.address) {
			return true;
		} else {
			return false;
		}
	}



	//新增收货地址
	$scope.addAddress=function(){
		cartService.addAddress($scope.addressList).success(
			function(response){
				if(response.success){
					alert('新增收货地址成功');
					$scope.addressList={};
				}else{
					alert(response.message);
				}
			}
		);
	}
});