
package org.skr.a.appsvr;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "registry")
public interface RegistryClient {

	@PostMapping("/registry/appsvr")
	void registerAppSvr(@RequestBody Map<String, Object> appSvr);

	@PostMapping("/registry/permission/{appSvrCode}")
	void registerPermission(@PathVariable(name = "appSvrCode") String appSvrCode,
							@RequestBody List<Map<String, Object>> permissions);

	@GetMapping("/registry/permissions")
	List<Map<String, Object>> listPermissions();

	@PostMapping("/registry/site-url/{appSvrCode}")
	void registerSiteUrl(@PathVariable(name = "appSvrCode") String appSvrCode,
						 @RequestBody List<Map<String, Object>> SiteUrls);

}