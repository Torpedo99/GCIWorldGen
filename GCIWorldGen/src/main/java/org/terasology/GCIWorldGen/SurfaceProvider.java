/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.GCIWorldGen;

import org.terasology.entitySystem.Component;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.*;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

@Produces(SurfaceHeightFacet.class)
public class SurfaceProvider implements ConfigurableFacetProvider {

    private static class SurfaceConfiguration implements Component
    {
        @Range(min = 5f, max = 40f, increment = 1f, precision = 1, description = "Surface Variation Height")
        private float surfaceHeight = 12;
    }

    SurfaceConfiguration configuration = new SurfaceConfiguration();

    private Noise noise;

    @Override
    public void setSeed(long seed) {
        noise = new SubSampledNoise(new SimplexNoise(seed), new Vector2f(0.01f, 0.01f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(SurfaceHeightFacet.class);
        SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(), border);

        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position: processRegion.contents()) {
            facet.setWorld(position, noise.noise(position.x(), position.y()) * configuration.surfaceHeight);
        }

        region.setRegionFacet(SurfaceHeightFacet.class, facet);
    }

    @Override
    public String getConfigurationName()
    {
        return "Surface Variation";
    }

    @Override
    public Component getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration)
    {
        this.configuration = (SurfaceConfiguration)configuration;
    }
}
