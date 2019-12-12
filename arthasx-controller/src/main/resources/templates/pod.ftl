<html>
<head>
</head>
<body>
	<h1>welcome , search your pods</h1>
	
	<form action="/ui/v1/arthasx/kubernetes/pods">
		<input name="labels" value="${labels}" placeholder="please input pod labels for search, for example:app=app1,node=node1" size="100" >
		<input type="submit" value="submit">
	</form>
	
	<#if (pods?size>0)>
		<h2>please select a pod </h2>
		
	    <table border="1">
			<tr>
				<td width='20%'><h3>pod</h3></td>
				<td width='20%'><h3>k8s-container-name</h3></td>
				<td width='20%'><h3>containerStatus</h3></td>
				<td><h3>ip</h3></td>
				<td><h3>node</h3></td>
				<td><h3>action</h3></td>
			</tr>
			<#if (pod.status.containerStatuses)??>
			<#list pod.status.containerStatuses as containerStatus> 
						<td>${containerStatus.name}</td>
						
					</#list>
			</#if>
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
			
		</table>
	</#if>
</body>
</html>