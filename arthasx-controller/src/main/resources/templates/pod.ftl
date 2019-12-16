<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<!-- Tell the browser to be responsive to screen width -->
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="description" content="">
	<meta name="author" content="">
	<!-- Favicon icon -->
	<link rel="icon" type="image/png" sizes="16x16" href="/assets/images/favicon.png">
	<title>Admin Pro Admin Template - The Ultimate Bootstrap 4 Admin Template</title>
	<!-- Bootstrap Core CSS -->
	<link href="/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	<!-- Custom CSS -->
	<link href="/assets/css/style.css" rel="stylesheet">
	<!-- You can change the theme colors from here -->
	<link href="/assets/css/colors/default-dark.css" id="theme" rel="stylesheet">
	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
	<script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
	<![endif]-->
</head>

<body class="fix-header card-no-border fix-sidebar">
<!-- ============================================================== -->
<!-- Preloader - style you can find in spinners.css -->
<!-- ============================================================== -->
<div class="preloader">
	<div class="loader">
		<div class="loader__figure"></div>
		<p class="loader__label">Admin Pro</p>
	</div>
</div>
<!-- ============================================================== -->
<!-- Main wrapper - style you can find in pages.scss -->
<!-- ============================================================== -->
<div id="main-wrapper">
	<!-- ============================================================== -->
	<!-- Topbar header - style you can find in pages.scss -->
	<!-- ============================================================== -->
	<header class="topbar">
		<nav class="navbar top-navbar navbar-expand-md navbar-light">
			<!-- ============================================================== -->
			<!-- Logo -->
			<!-- ============================================================== -->
			<div class="navbar-header">
				<a class="navbar-brand" href="#">
					<!-- Logo icon --><b>
						<img src="/assets/images/logo-icon.png" alt="homepage" class="dark-logo" />
					</b>
					<!--End Logo icon -->
					<!-- Logo text -->
					<span>
                            <img src="/assets/images/logo-text.png" alt="homepage" class="dark-logo" />
                        </span>
				</a>
			</div>
			<!-- ============================================================== -->
			<!-- End Logo -->
			<!-- ============================================================== -->
			<div class="navbar-collapse">
				<!-- ============================================================== -->
				<!-- toggle and nav items -->
				<!-- ============================================================== -->
				<ul class="navbar-nav mr-auto">
					<!-- This is  -->
					<li class="nav-item"> <a class="nav-link nav-toggler hidden-md-up waves-effect waves-dark" href="javascript:void(0)"><i class="ti-menu"></i></a> </li>
				</ul>
				<!-- ============================================================== -->
				<!-- User profile and search -->
				<!-- ============================================================== -->
				<ul class="navbar-nav my-lg-0">
					<!-- ============================================================== -->
					<!-- Search -->
					<!-- ============================================================== -->
					<li class="nav-item hidden-xs-down search-box"> <a class="nav-link hidden-sm-down waves-effect waves-dark" href="javascript:void(0)"><i class="ti-search"></i></a>
						<form class="app-search" action="/ui/v1/arthasx/kubernetes/pods" id="searchForm">
							<input type="text" name="labels" value="${labels}" class="form-control" placeholder="please input pod labels for search, for example:app=app1,node=node1"> <a href="javascript:void(0)" class="srh-btn"><i class="ti-close"></i></a>
						</form>
					</li>
					<!-- ============================================================== -->
					<!-- Profile -->
					<!-- ============================================================== -->
					<li class="nav-item">
						<a class="nav-link waves-effect waves-dark" href="#"><img src="/assets/images/users/1.jpg" alt="user" class="profile-pic" /></a>
					</li>
				</ul>
			</div>
		</nav>
	</header>
	<!-- ============================================================== -->
	<!-- End Topbar header -->
	<!-- ============================================================== -->
	<!-- ============================================================== -->
	<!-- Left Sidebar - style you can find in sidebar.scss  -->
	<!-- ============================================================== -->
	<aside class="left-sidebar">
		<!-- Sidebar scroll-->
		<div class="scroll-sidebar">
			<!-- Sidebar navigation-->
			<nav class="sidebar-nav">
				<ul id="sidebarnav">
					<#--                        <li> <a class="waves-effect waves-dark" href="index.html" aria-expanded="false"><i class="mdi mdi-gauge"></i><span class="hide-menu">Dashboard</span></a></li>-->
					<li> <a class="waves-effect waves-dark" href="#" aria-expanded="false"><i class="mdi mdi-playstation"></i><span class="hide-menu">pods</span></a></li>
				</ul>
			</nav>
			<!-- End Sidebar navigation -->
		</div>
		<!-- End Sidebar scroll-->
	</aside>
	<!-- ============================================================== -->
	<!-- End Left Sidebar - style you can find in sidebar.scss  -->
	<!-- ============================================================== -->
	<!-- ============================================================== -->
	<!-- Page wrapper  -->
	<!-- ============================================================== -->
	<div class="page-wrapper">
		<!-- ============================================================== -->
		<!-- Container fluid  -->
		<!-- ============================================================== -->
		<div class="container-fluid">
			<!-- ============================================================== -->
			<!-- Bread crumb and right sidebar toggle -->
			<!-- ============================================================== -->
			<div class="row page-titles">
				<div class="col-md-5 align-self-center">
					<h3 class="text-themecolor">pod table</h3>
				</div>
			</div>
			<!-- ============================================================== -->
			<!-- End Bread crumb and right sidebar toggle -->
			<!-- ============================================================== -->
			<!-- ============================================================== -->
			<!-- Start Page Content -->
			<!-- ============================================================== -->
			<div class="row">
				<!-- column -->
				<div class="col-lg-12">
					<div class="card">
						<div class="card-body">
							<h4 class="card-title">pod table</h4>
							<h6 class="card-subtitle">pod <code>.table</code></h6>
							<div class="table-responsive">
								<table class="table">
									<thead>
									<tr>
										<th>pod</th>
										<th>k8s-container-name</th>
										<th>containerStatus</th>
										<th>ip</th>
										<th>node</th>
										<th>action</th>
										<#if (pod.status.containerStatuses)??>
											<#list pod.status.containerStatuses as containerStatus>
												<th>${containerStatus.name}</th>
											</#list>
										</#if>
									</tr>
									</thead>
									<tbody>
									<#list pods as pod>
										<#list pod.spec.containers as container>
											<tr>
												<td>${pod.metadata.name}</td>
												<td>${container.name}</td>
												<#list pod.status.containerStatuses as containerStatus>
													<#if containerStatus.name == container.name>
														<#if (containerStatus.state.running)??>
															<td style="color:green">running</td>
														</#if>
														<#if (containerStatus.state.waiting)??>
															<td><span style="color:yellow">waiting</span>,reason:${(containerStatus.state.waiting.reason)!""},message:${(containerStatus.state.waiting.message)!""}</td>
														</#if>
														<#if (containerStatus.state.terminated)??>
															<td style="color:red">terminated</td>
														</#if>
														<td>${(pod.status.podIP)!}</td>
														<td>${pod.spec.nodeName}</td>
														<td>
															<#if (pod.status.podIP)??>
																<#list container.volumeMounts as volumeMount>
																	<#if volumeMount.name == 'arthasxhome'>
																		<a href="/ui/v1/arthasx/kubernetes/attach?namespace=${pod.metadata.namespace}&pod=${pod.metadata.name}&containerName=${container.name}&containerIp=${pod.status.podIP}&arthasxhome=${volumeMount.mountPath}&jpsKeywords=${(pod.metadata.annotations.arthasxjpskeywords)!"app.jar"}" target="_blank">Diagnosis</a>
																	</#if>
																</#list>
															</#if>
														</td>
													</#if>
												</#list>
											</tr>
										</#list>
									</#list>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- ============================================================== -->
			<!-- End PAge Content -->
			<!-- ============================================================== -->
		</div>
		<!-- ============================================================== -->
		<!-- End Container fluid  -->
		<!-- ============================================================== -->
		<!-- ============================================================== -->
		<!-- footer -->
		<!-- ============================================================== -->
		<footer class="footer"> © 2017 Admin Pro by wrappixel - More Templates <a href="http://www.cssmoban.com/" target="_blank" title="模板之家">模板之家</a> - Collect from <a href="http://www.cssmoban.com/" title="网页模板" target="_blank">网页模板</a> </footer>
		<!-- ============================================================== -->
		<!-- End footer -->
		<!-- ============================================================== -->
	</div>
	<!-- ============================================================== -->
	<!-- End Page wrapper  -->
	<!-- ============================================================== -->
</div>
<!-- ============================================================== -->
<!-- End Wrapper -->
<!-- ============================================================== -->
<!-- ============================================================== -->
<!-- All Jquery -->
<!-- ============================================================== -->
<script src="/assets/plugins/jquery/jquery.min.js"></script>
<!-- Bootstrap tether Core JavaScript -->
<script src="/assets/plugins/bootstrap/js/popper.min.js"></script>
<script src="/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
<!-- slimscrollbar scrollbar JavaScript -->
<script src="/assets/js/perfect-scrollbar.jquery.min.js"></script>
<!--Wave Effects -->
<script src="/assets/js/waves.js"></script>
<!--Menu sidebar -->
<script src="/assets/js/sidebarmenu.js"></script>
<!--Custom JavaScript -->
<script src="/assets/js/custom.min.js"></script>
</body>

</html>