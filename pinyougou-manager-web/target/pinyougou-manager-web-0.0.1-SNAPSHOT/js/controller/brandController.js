	//品牌控制层
    	app.controller('brandController',function($scope,$http,brandService){
    		
    	//读取列表数据绑定到表单中
    	$scope.findAll=function(){
    		brandService.findAll().success(
    			function(response){
    				$scope.list=response;
    			}		
    		);
    	}
    	
    	//刷新列表
		$scope.reloadList=function(){
			$scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage); 
		}
    	
    	//分页控件配置
    	//currentPage:当前页 
    	//totalItems：总记录数
		//itemsPerPage：每页记录数
		//perPageOptions：分页选项
		//onChange：当页码重新变更后自动触发的方法
    	$scope.paginationConf = { 
    			 currentPage: 1, 
    			 totalItems: 10, 
    			 itemsPerPage: 10, 
    			 perPageOptions: [10, 20, 30, 40, 50], 
    			 onChange: function(){ 
    				 $scope.reloadList();
    			 }
    		};
    		
    		
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
    		
    		
    		$scope.selectIds=[];//选中的ID集合
    		//用户勾选复选框
    		$scope.updateSelection=function($event,id){
    			if($event.target.checked){  //如果是被选中，则增加到数组
    				$scope.selectIds.push(id);//push向集合添加元素
    			}else{
    				var index=$scope.selectIds.indexOf(id);//查找值的位置
    				$scope.selectIds.splice(index,1);//（移除的位置，移除的个数）
    			}
    			
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