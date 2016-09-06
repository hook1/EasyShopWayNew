var userApp = angular.module('MyApp');
      
userApp.config(function($routeProvider) {
	$routeProvider.when("/", {
		templateUrl : "template/shared/info.jsp"
	}).when("/statistic", {
		templateUrl : "template/shared/statistic.jsp",
		controller : "ChartCtrl", 
	}).when("/history", {
		templateUrl : "template/shared/history.jsp",
		controller : "UserHistoryCtrl"
	});
});

userApp.controller('ChartCtrl', ['$scope', '$http', function($scope, $http) {
	$scope.date = new Date();
	console/log("asd");
	$scope.startDate = new Date(
		      $scope.date.getFullYear(),
		      $scope.date.getMonth() - 1,
		      $scope.date.getDate());

	$scope.endDate = new Date();
		  
	//alert($scope.startDate);
	$scope.getFoodData = function () {
		var responsik = undefined;
		var startDate = moment($scope.startDate).format('YYYY-MM-DD');
		var endDate = moment($scope.endDate).format('YYYY-MM-DD');
		 var data = $.param({
	            startDate: startDate,
	            endDate: endDate  
	     });
		 
		 var config = {
		            headers: {
		                'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
		            }
		        }
		 
		 $http.post('http://localhost:8080/EasyShopWayNew/userStat', data, config)
		 		.success(function (data, status, headers, config) {
		 			   response = data;
		        	   console.log(response);
		        	   $('#container').highcharts({
		        		      chart: {
		        		          plotBackgroundColor: null,
		        		          plotBorderWidth: null,
		        		          plotShadow: false,
		        		          type: 'pie'
		        		      },
		        		      title: response.title,
		        		      tooltip: {
		        		          pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		        		      },
		        		      plotOptions: {
		        		          pie: {
		        		              allowPointSelect: true,
		        		              cursor: 'pointer',
		        		              dataLabels: {
		        		                  enabled: false
		        		                  
		        		              },
		        		              showInLegend: true
		        		          }
		        		      },
		        		      series: response.series
		        	      });
	
	         }).error(
	             function (data, status, header, config) {
	                 console.log('fail');
	             });

		
//		//Build the chart
//        $http({
//        	  method: 'POST',
//        	  url: '/EasyShopWayNew/userStat'
//        	}).then(function successCallback(response) {
//        	   responsik = response.data;
//        	   console.log(responsik);
//        	   $('#container').highcharts({
//        		      chart: {
//        		          plotBackgroundColor: null,
//        		          plotBorderWidth: null,
//        		          plotShadow: false,
//        		          type: 'pie'
//        		      },
//        		      title: responsik.title,
//        		      tooltip: {
//        		          pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
//        		      },
//        		      plotOptions: {
//        		          pie: {
//        		              allowPointSelect: true,
//        		              cursor: 'pointer',
//        		              dataLabels: {
//        		                  enabled: false
//        		                  
//        		              },
//        		              showInLegend: true
//        		          }
//        		      },
//        		      series: responsik.series
//        	      });
//        	  }, function errorCallback(response) {
//        	    console.log("fail");
//        	  });
    };
}]);