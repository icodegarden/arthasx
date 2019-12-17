<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="description" content="">
	<meta name="author" content="">
	<title>arthasx</title>
<!-- 	<link rel="icon" type="image/png" sizes="16x16" href="/assets/images/favicon.png"> -->
	<link href="/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	<link href="/assets/css/style.css" rel="stylesheet">
	<link href="/assets/css/colors/default-dark.css" id="theme" rel="stylesheet">
</head>

<body class="fix-header card-no-border fix-sidebar">
<div id="main-wrapper">
	<header class="topbar">
		<nav class="navbar top-navbar navbar-expand-md navbar-light">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">
					<!-- Logo icon --><b>
<!-- 						<img src="/assets/images/logo-icon.png" alt="homepage" class="dark-logo" /> -->
					</b>
					<span>
<!--                         <img src="/assets/images/logo-text.png" alt="homepage" class="dark-logo" /> -->
						arthasx
                    </span>
				</a>
			</div>
			<div class="navbar-collapse">
				<ul class="navbar-nav mr-auto">
					<li class="nav-item"> <a class="nav-link nav-toggler hidden-md-up waves-effect waves-dark" href="javascript:void(0)"><i class="ti-menu"></i></a> </li>
				</ul>
				<ul class="navbar-nav my-lg-0">
					<li class="nav-item hidden-xs-down search-box"> <a class="nav-link hidden-sm-down waves-effect waves-dark" href="javascript:void(0)"><i class="ti-search"></i></a>
						<form class="app-search" action="/ui/v1/arthasx/kubernetes/pods" id="searchForm">
							<input type="text" name="labels" value="${labels}" class="form-control" placeholder="please input pod labels for search, for example:app=app1,node=node1"> <a href="javascript:void(0)" class="srh-btn"><i class="ti-close"></i></a>
						</form>
					</li>
					<li class="nav-item">
<!-- 						<a class="nav-link waves-effect waves-dark" href="#"><img src="/assets/images/users/1.jpg" alt="user" class="profile-pic" /></a> -->
					</li>
				</ul>
			</div>
		</nav>
	</header>
	<aside class="left-sidebar">
		<div class="scroll-sidebar">
			<nav class="sidebar-nav">
				<ul id="sidebarnav">
<!-- 					<li> <a class="waves-effect waves-dark" href="index.html" aria-expanded="false"><i class="mdi mdi-gauge"></i><span class="hide-menu">Dashboard</span></a></li> -->
<!-- 					<li> <a class="waves-effect waves-dark" href="#" aria-expanded="false"><i class="mdi mdi-playstation"></i><span class="hide-menu">pods</span></a></li> -->
					<li> <a class="waves-effect waves-dark" href="#" aria-expanded="false"><span class="hide-menu">pods</span></a></li>
				</ul>
			</nav>
		</div>
	</aside>
	<div class="page-wrapper">
		<div class="container-fluid">
			<div class="row page-titles">
				<div class="col-md-5 align-self-center">
					<h3 class="text-themecolor">pods</h3>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="card">
						<div class="card-body">
<!-- 							<h4 class="card-title">pod table</h4> -->
<!-- 							<h6 class="card-subtitle">pod <code>.table</code></h6> -->
							<div class="table-responsive">
								<table class="table">
									<thead>
									<tr>
										<th>pod</th>
										<th>container name</th>
										<th>container status</th>
										<th>container ip</th>
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
		</div>
		<footer class="footer"></footer>
	</div>
</div>
<script src="/assets/plugins/jquery/jquery.min.js"></script>
<script src="/assets/plugins/bootstrap/js/popper.min.js"></script>
<script src="/assets/plugins/bootstrap/js/bootstrap.min.js"></script>
<script src="/assets/js/perfect-scrollbar.jquery.min.js"></script>
<script src="/assets/js/waves.js"></script>
<script src="/assets/js/sidebarmenu.js"></script>
<script src="/assets/js/custom.min.js"></script>
</body>

</html>