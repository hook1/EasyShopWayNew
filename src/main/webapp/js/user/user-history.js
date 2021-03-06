/**
 * 
 */

var userHistoryApp = angular.module("MyApp");

userHistoryApp.controller("UserHistoryCtrl", ['$scope', '$http',
						function ($scope, $http) {

                            $scope.sendToMap = function (item) {
                                console.log(item);
                                
                                var config = {
                                        headers: {
                                            'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
                                        }
                                };
                                
                                var data = $.param(item);
                                
                                
                                
                                $http
                                .get(
                                    '/EasyShopWayNew/history',
                                    config)
                                .success(
                                    function (data, status, headers,
                                        config) {
                                        $scope.history = data.lists;
                                        console.log("Load " + $scope.history.products);
                                    }).error(
                                    function (data, status, header,
                                        config) {
                                        console.log('fail');
                                    });
                            };


        var config = {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
            }
        }

        $http
            .get(
                '/EasyShopWayNew/history',
                config)
            .success(
                function (data, status, headers,
                    config) {
                    $scope.history = data.lists;
                    console.log($scope.history);
                    console.log($scope.history.length);
                    
                }).error(
                function (data, status, header,
                    config) {
                    console.log('fail');
                });


}]);