app.controller('searchController', function($scope, searchService) {

	// 定义搜索对象的结构
	$scope.searchMap = {
		'keywords' : '',
		'category' : '',
		'brand' : '',
		'spec' : {},
		'price':''
	};
	// 搜索
	$scope.search = function() {
		searchService.search($scope.searchMap).success(function(response) {
			$scope.resultMap = response;// 搜索返回的结果
		});
	}
	// 添加复合搜索条件
	$scope.addSearchItem = function(key, value) {
		if (key == 'category' || key == 'brand' || key=='price') {//如果用户点击的分类或品牌或价格
			$scope.searchMap[key] = value;
		} else {//用户选择是规格
			$scope.searchMap.spec[key] = value;
		}
		//执行搜索
		$scope.search();
	}
	//移除符合搜索条件
	$scope.removeSearchItem=function(key){
		if(key=='category' || key=='brand' || key=='price'){//如果用户点击的分类或品牌
			$scope.searchMap[key]="";
		}else{//用户选择是规格
			delete $scope.searchMap.spec[key];//移除此属性
		}
		$scope.search();
	}
		

});