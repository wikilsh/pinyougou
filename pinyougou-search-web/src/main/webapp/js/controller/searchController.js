app.controller('searchController', function($scope, searchService) {

	// 定义搜索对象的结构
	$scope.searchMap = {
		'keywords' : '',
		'category' : '',
		'brand' : '',
		'spec' : {},
		'price':'',
		'pageNo':1,
		'pageSize':40
	};//搜索条件封装对象
	
	// 搜索
	$scope.search = function() {
		$scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(function(response) {
			$scope.resultMap = response;// 搜索返回的结果
			buildPageLabel();//调用 构建分页栏
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
	
	//构建分页标签（totalPages为总页数）
	buildPageLabel=function(){
		$scope.pageLabel=[];
		var maxPageNo=$scope.resultMap.totalPages;//得到最后页码
		var firstPage=1;//开始页码
		var lastPage=maxPageNo;//截至页码
		$scope.firstDot=true;//前面有点
		$scope.lastDot=true;//后面有点
		
		if($scope.resultMap.totalPages>5){//如果总页数大于5页 显示部分页码
			if($scope.searchMap.pageNo<=3){//如果当前页小于等于3页
				lastPage=5;//前5页
				$scope.firstDot=false;
			}else if($scope.searchMap.pageNo>=lastPage-2){//如果当前页大于等于最大页码-2
				firstPage=maxPageNo-4;//后5页
				$scope.lastDot=false;
			}else{//显示页为中心的5页
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
				$scope.firstDot=false;
				$scope.lastDot=false;
			}
		}
		
		//循环产生页码标签
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);
		}
		
	}
	//根据页码查询
	$scope.queryByPage=function(pageNo){
		//页码验证
		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return
		}
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	//判断当前页是否为第一页
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}
		
	}
	//
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.searchMap.totalPages){
			return true;
		}else{
			return false;
		}
		
	}
	

});