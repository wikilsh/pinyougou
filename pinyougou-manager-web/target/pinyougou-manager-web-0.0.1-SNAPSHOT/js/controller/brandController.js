//控制器
	app.controller('brandController',function($scope,$controller,brandService){
		
	//$controller也是angular提供的一个服务，可以实现伪继承，实际上就是与BaseController共享$scope
	$controller('baseController',{$scope:$scope});//继承baseController.js中的共用功能（分页、复选...）
    
	//读取列表数据绑定到表单中
	$scope.findAll=function(){
    	brandService.findAll().success(
    		function(response){
   				$scope.list=response;
   			}		
   		);
   	}

	//分页
   	$scope.findPage=function(page,size){
   			brandService.findPage(page,size).success(
   				function(response){
   					$scope.list=response.rows;//显示当前页的数据
   					$scope.paginationConf.totalItems=response.total;//更新总记录数
   				}		
   			);
   		}
   		
   	//品牌增加
   	$scope.save=function(){
   		var object=null;
   		if($scope.entity.id!=null){
   			object=brandService.update($scope.entity);
   		}else{
   			object=brandService.add($scope.entity);
   		}
   		object.success(
   			function(response){
   				if(response.success){
   					$scope.reloadList();//刷新页面
   				}else{
   					alert(response.success);
   				}
   			}		
   		)
   	}
   		
   		//查询实体
   		$scope.findOne=function(id){
   			brandService.findOne(id).success(
   				function(response){ 
   					$scope.entity=response;
   				}		
   			);
   		}
   		
  		
   		//删除所选品牌
   		$scope.dele=function(){
   			brandService.dele($scope.selectIds).success(
   				function(response){
   					if(response.success){
   						$scope.reloadList();//刷新列表
   					}
   				}		
   			);
   			
   		}
   		
   		$scope.searchEntity={};//定义搜索对象
  		//条件查询
   		$scope.search=function(page,size){
   			
   			brandService.search(page,size,$scope.searchEntity).success(
       			function(response){
       				$scope.list=response.rows;//显示当前页的数据
       				$scope.paginationConf.totalItems=response.total;//更新总记录数
       			}		
       		);
   			
   		}
    		
  });